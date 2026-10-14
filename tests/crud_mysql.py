import concurrent.futures
import json
import sys
import uuid
from pathlib import Path
from urllib.request import Request, urlopen
from urllib.error import HTTPError

BASE = (sys.argv[1] if len(sys.argv) > 1 else 'http://localhost:18081').rstrip('/') + '/api'
REPORT = Path(sys.argv[2] if len(sys.argv) > 2 else 'reporte-crud.json')
results = []
created = []
tag = uuid.uuid4().hex[:10]


def raw(method, path, body=None):
    payload = None if body is None else json.dumps(body).encode('utf-8')
    request = Request(BASE + path, data=payload, method=method,
                      headers={'Content-Type': 'application/json'})
    try:
        response = urlopen(request, timeout=20)
    except HTTPError as error:
        response = error
    data = response.read().decode('utf-8')
    return response.code, json.loads(data) if data else None, response.headers


def call(name, method, path, body=None, expected=200):
    status, data, headers = raw(method, path, body)
    results.append({'prueba': name, 'esperado': expected, 'obtenido': status, 'ok': status == expected})
    assert status == expected, (name, status, data)
    if status >= 400:
        check(name + ': error estructurado', isinstance(data, dict) and data.get('status') == status)
    if method == 'POST' and status == 201:
        created.append((path, data['id']))
        check(name + ': Location', headers.get('Location') == '/api' + path + '/' + str(data['id']))
    return data


def check(name, condition):
    results.append({'prueba': name, 'ok': bool(condition)})
    assert condition, name


def crud(resource, body, changed):
    data = call(resource + ': crear', 'POST', '/' + resource, body, 201)
    path = '/' + resource + '/' + str(data['id'])
    check(resource + ': aparece en lista', any(x['id'] == data['id'] for x in call(resource + ': listar', 'GET', '/' + resource)))
    check(resource + ': consultar', call(resource + ': obtener', 'GET', path)['id'] == data['id'])
    updated = call(resource + ': actualizar', 'PUT', path, body | changed)
    for key, value in changed.items():
        check(resource + ': persistir ' + key, updated[key] == value and call(resource + ': releer ' + key, 'GET', path)[key] == value)
    return data['id']


try:
    oficina = dict(nombreOficina='Prueba ' + tag, direccion='Calle 1', ciudad='Ciudad', region='Region')
    oid = crud('oficinas', oficina, {'ciudad': 'Ciudad actualizada'})
    cliente = dict(nombre='Ana', apellido='Prueba', email=tag+'@example.com', licencia='LIC'+tag, telefono='+525512345678')
    cid = crud('clientes', cliente, {'nombre': 'Ana actualizada'})
    vehiculo = dict(marca='Toyota', modelo='Prueba', anio=2024, placa='T'+tag.upper(), tarifaDiaria=100, estado='DISPONIBLE', oficinaId=oid)
    vid = crud('vehiculos', vehiculo, {'modelo': 'Actualizado'})
    renta = dict(vehiculoId=vid, clienteId=cid, fechaInicio='2027-01-01', fechaFin='2027-01-04', estado='PENDIENTE')
    rid = crud('alquileres', renta, {'fechaFin': '2027-01-05'})
    check('precio automático 4 días', call('precio', 'GET', '/alquileres/'+str(rid))['precioTotal'] == 400)
    mantenimiento = dict(vehiculoId=vid, fechaMantenimiento='2026-10-13', descripcion='Revision', costo=20)
    mid = crud('mantenimientos', mantenimiento, {'descripcion': 'x'*1000})
    call('rechaza descripción 1001', 'POST', '/mantenimientos', mantenimiento | {'descripcion': 'x'*1001}, 400)
    for resource in ['oficinas', 'clientes', 'vehiculos', 'alquileres', 'mantenimientos']:
        call(resource + ': cuerpo vacío', 'POST', '/'+resource, {}, 400)
        call(resource + ': id negativo', 'GET', '/'+resource+'/-1', expected=400)
        for method in ['GET', 'PUT', 'DELETE']:
            body = {'oficinas': oficina, 'clientes': cliente, 'vehiculos': vehiculo, 'alquileres': renta, 'mantenimientos': mantenimiento}[resource] if method == 'PUT' else None
            call(resource + ': inexistente '+method, method, '/'+resource+'/9223372036854775807', body, 404)
    call('email duplicado', 'POST', '/clientes', cliente | {'licencia': 'OTRA'+tag}, 409)
    call('licencia duplicada', 'POST', '/clientes', cliente | {'email': 'otro'+tag+'@example.com'}, 409)
    call('placa duplicada', 'POST', '/vehiculos', vehiculo, 409)
    call('email inválido', 'POST', '/clientes', cliente | {'email': 'invalido'}, 400)
    call('tarifa negativa', 'POST', '/vehiculos', vehiculo | {'tarifaDiaria': -1}, 400)
    call('costo negativo', 'POST', '/mantenimientos', mantenimiento | {'costo': -1}, 400)
    call('fechas invertidas', 'POST', '/alquileres', renta | {'fechaFin': '2026-01-01'}, 400)
    call('estado inválido', 'POST', '/vehiculos', vehiculo | {'estado': 'OTRO'}, 400)
    for resource, body, key in [('vehiculos',vehiculo,'oficinaId'), ('alquileres',renta,'vehiculoId'), ('alquileres',renta,'clienteId'), ('mantenimientos',mantenimiento,'vehiculoId')]:
        call('FK inexistente '+resource+' '+key, 'POST', '/'+resource, body | {key: 9223372036854775807}, 400)
    call('reserva solapada', 'POST', '/alquileres', renta | {'fechaInicio': '2027-01-03'}, 409)
    adjacent = call('reserva contigua permitida', 'POST', '/alquileres', renta | {'fechaInicio': '2027-01-05', 'fechaFin': '2027-01-07'}, 201)
    rp = '/alquileres/'+str(rid)
    current = renta | {'fechaFin': '2027-01-05'}
    call('activar', 'PUT', rp, current | {'estado': 'ACTIVO'})
    check('vehículo alquilado', call('estado activo', 'GET', '/vehiculos/'+str(vid))['estado'] == 'ALQUILADO')
    call('segundo activo rechazado', 'PUT', '/alquileres/'+str(adjacent['id']), renta | {'fechaInicio': '2027-01-05', 'fechaFin': '2027-01-07', 'estado': 'ACTIVO'}, 409)
    call('completar', 'PUT', rp, current | {'estado': 'COMPLETADO'})
    check('vehículo disponible', call('estado completado', 'GET', '/vehiculos/'+str(vid))['estado'] == 'DISPONIBLE')
    call('marcar mantenimiento', 'PUT', '/vehiculos/'+str(vid), vehiculo | {'estado':'MANTENIMIENTO'})
    call('mantenimiento rechaza pendiente', 'POST', '/alquileres', renta, 409)
    call('mantenimiento rechaza activo', 'POST', '/alquileres', renta | {'estado':'ACTIVO'}, 409)
    call('restaurar disponibilidad', 'PUT', '/vehiculos/'+str(vid), vehiculo)
    call('reactivar', 'PUT', rp, current | {'estado':'ACTIVO'})
    second = call('otro vehículo', 'POST', '/vehiculos', vehiculo | {'placa':'B'+tag.upper()}, 201)
    call('cambiar vehículo activo', 'PUT', rp, current | {'estado':'ACTIVO','vehiculoId':second['id']})
    check('anterior liberado', call('estado anterior', 'GET','/vehiculos/'+str(vid))['estado']=='DISPONIBLE')
    check('nuevo alquilado', call('estado nuevo', 'GET','/vehiculos/'+str(second['id']))['estado']=='ALQUILADO')
    call('eliminar activo', 'DELETE', rp, expected=204)
    check('eliminación libera vehículo', call('estado tras eliminar', 'GET','/vehiculos/'+str(second['id']))['estado']=='DISPONIBLE')
    call('eliminado inexistente', 'GET', rp, expected=404)
    concurrent_body = renta | {'fechaInicio':'2028-01-01','fechaFin':'2028-01-04'}
    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        responses = list(pool.map(lambda _: raw('POST','/alquileres',concurrent_body), range(2)))
    for status, data, _ in responses:
        if status == 201:
            created.append(('/alquileres',data['id']))
    check('concurrencia: una reserva y un conflicto', sorted(x[0] for x in responses)==[201,409])
    call('no borrar vehículo referenciado','DELETE','/vehiculos/'+str(vid),expected=409)
    call('no borrar oficina referenciada','DELETE','/oficinas/'+str(oid),expected=409)
    call('no borrar cliente referenciado','DELETE','/clientes/'+str(cid),expected=409)
finally:
    for resource in ['/alquileres','/mantenimientos','/vehiculos','/clientes','/oficinas']:
        for path, identifier in reversed(created):
            if path == resource:
                target = path+'/'+str(identifier)
                status, _, _ = raw('DELETE',target)
                if status != 404:
                    results.append({'prueba':path+': eliminar','ok':status==204,'obtenido':status})
                    status_after, _, _ = raw('GET',target)
                    results.append({'prueba':path+': inexistente después de eliminar','ok':status_after==404})
    REPORT.parent.mkdir(parents=True,exist_ok=True)
    REPORT.write_text(json.dumps({'base':BASE,'total':len(results),'aprobadas':sum(x['ok'] for x in results),'pruebas':results},ensure_ascii=False,indent=2),encoding='utf-8')
    print(str(REPORT),len(results),'comprobaciones;',sum(x['ok'] for x in results),'aprobadas')
    if not all(x['ok'] for x in results):
        raise SystemExit(1)
