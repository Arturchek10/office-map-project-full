export type CurrentOffice = {
  id: number;
  name: string;
  startFloor: {
    id: number;
    name: string;
    orderNumber: number;
  },
  floors: {
    id: number;
    name: string;
    orderNumber: number;
  }[]
}