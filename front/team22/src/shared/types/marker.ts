export type MarkerRequest = {
  type: MarkerTypes;
  position: {
    position_x: number;
    position_y: number;
  };
};

export type MarkerResponse = {
  id: number;
  type: MarkerTypes;
  position: {
    position_x: number;
    position_y: number;
  };
  payload?: Record<string, any>; // сервер возвращает payload только если он был заполнен ранее
};

export type workspaceMarker = MarkerResponse & {
  uncomfortable: boolean;
  payload?: {
    haveComputer: boolean;
  };
};

export type RoomMarker = MarkerResponse & {
  payload?: {
    capacity: number;
  };
};

export type UtilityMarker = MarkerResponse & {
  payload?: {
    description: string;
  };
};

export type EmergencyMarker = MarkerResponse & {
  payload?: {
    instruction: string;
  };
};

export type Marker =
  | workspaceMarker
  | RoomMarker
  | UtilityMarker
  | EmergencyMarker;

export type MarkerTypes = "workspace" | "room" | "emergency" | "utility";

export const markerColor: Record<MarkerTypes, string> = {
  workspace: "#3A7EFC",
  room: "#5EF821",
  emergency: "#B70000",
  utility: "#878B8D",
};



export const strokeColor: Record<MarkerTypes, string> = {
  workspace: "#0431AE",
  room: "#51AB2C",
  emergency: "#870000",
  utility: "#5D5E5E",
};
