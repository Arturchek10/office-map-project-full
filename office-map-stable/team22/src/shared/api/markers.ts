
import {
  Marker,
  MarkerRequest,
  MarkerResponse,
  MarkerTypes,
} from "@shared/types/marker"
import { apiPost, apiGet, apiDelete, apiPatch } from "@shared/utils/api"

type addMarkerParams = {
  marker: MarkerRequest
  layerId: number
}

// Добавление нового маркера на слой
export const addMarker = async ({
  marker,
  layerId,
}: addMarkerParams): Promise<Marker> => {
  const res = await apiPost(`/api/v1/markers/${layerId}`, marker)
  return res.json()
}

// Получение маркера по ID
// markerId - ID маркера, который нужно получить
export const getMarker = async (markerId: number): Promise<MarkerResponse> => {
  const res = await apiGet(`/api/v1/markers/${markerId}`)
  return res.json()
}

type ReplaceMarkerParams = {
  markerId: number
  position: { position_x: number; position_y: number }
}

export const replaceMarker = async ({
  markerId,
  position,
}: ReplaceMarkerParams): Promise<Marker> => {
  const res = await apiPatch(`/api/v1/markers/move/${markerId}`, { position })
  return res.json()
}

// Удаление маркера по ID
export const deleteMarker = async (markerId: number): Promise<number> => {
  await apiDelete(`/api/v1/markers/${markerId}`)
  return markerId
}


// обновление данных маркера
type UpdateMarkerParams = {
  name: string;
  markerId: number;
  type: MarkerTypes;
  payload?: Record<string, any>;
  uncomfortable?: boolean;
}

export const updateMarker = async ({ markerId, name, type, payload, uncomfortable }: UpdateMarkerParams): Promise<MarkerResponse> => {
  const body: Record<string, any> = {}; // обязательно name в теле
  if (typeof name !== "undefined") body.name = name;
  if (typeof type !== "undefined") body.type = type;
  if (typeof payload !== "undefined") body.payload = payload;
  if (typeof uncomfortable !== "undefined") body.uncomfortable = uncomfortable;

  const res = await apiPatch(`/api/v1/markers/${markerId}`, body);

  if (!res.ok) {
    throw new Error(`Ошибка при обновлении маркера: ${res.status}`);
  }

  const data: MarkerResponse = await res.json();
  return data;
};