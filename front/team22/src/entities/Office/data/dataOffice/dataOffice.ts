import type { TOffice } from "@entities/Office/type/office";

export const mockOffices: TOffice[] = [
  {
    id: 50,
    name: 'Головной офис',
    latitude: 55.7558,
    longitude: 37.6176,
    photoUrl: 'https://i.pinimg.com/originals/d8/51/42/d85142b1389baddb13865c98d49d4828.jpg',
    city: 'Москва',
    address: 'ул. Тверская, д. 7',
  },
  {
    id: 2,
    name: 'Филиал в СПб',
    latitude: 59.9343,
    longitude: 30.3351,
    photoUrl: 'https://i.pinimg.com/736x/94/f5/64/94f5644ee3c5bc79cef8b3bdc7959909.jpg',
    city: 'Санкт-Петербург',
    address: 'Невский проспект, д. 28',
  },
  {
    id: 3,
    name: 'Офис в Казани',
    latitude: 55.7908,
    longitude: 49.1144,
    photoUrl: 'https://avatars.mds.yandex.net/get-altay/6145759/2a00000182d42c55052cb032487ef7943231/XXXL',
    city: 'Казань',
    address: 'ул. Баумана, д. 15',
  },
];