import NavbarItem from 'app/layouts/navbar/navbar-item.model';

export const EntityNavbarItems: NavbarItem[] = [
  {
    name: 'Cliente',
    route: '/cliente',
    translationKey: 'global.menu.entities.cliente',
  },
  {
    name: 'Habitaciones',
    route: '/habitaciones',
    translationKey: 'global.menu.entities.habitaciones',
  },
  {
    name: 'Reservaciones',
    route: '/reservaciones',
    translationKey: 'global.menu.entities.reservaciones',
  },
  {
    name: 'Factura',
    route: '/factura',
    translationKey: 'global.menu.entities.factura',
  },
  {
    name: 'Eventos',
    route: '/eventos',
    translationKey: 'global.menu.entities.eventos',
  },
  {
    name: 'Colaborador',
    route: '/colaborador',
    translationKey: 'global.menu.entities.colaborador',
  },
  {
    name: 'TipoCargo',
    route: '/tipo-cargo',
    translationKey: 'global.menu.entities.tipoCargo',
  },
];
