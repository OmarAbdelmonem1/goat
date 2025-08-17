import React from 'react';
import EntitiesMenuItems from 'app/entities/menu';
import { NavDropdown } from './menu-components';
import { useAppSelector } from 'app/config/store';
import { hasAnyAuthority } from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';

export const EntitiesMenu = () => {
  const isAuthenticated = useAppSelector(state => state.authentication.isAuthenticated);
  const account = useAppSelector(state => state.authentication.account);

  // Check if user has permission to see entities menu
  const canViewEntities = isAuthenticated && account && hasAnyAuthority(account.authorities, [AUTHORITIES.ADMIN, AUTHORITIES.USER]);

  if (!canViewEntities) {
    return null;
  }

  return (
    <NavDropdown icon="th-list" name="Entities" id="entity-menu" data-cy="entity" style={{ maxHeight: '80vh', overflow: 'auto' }}>
      {' '}
      <EntitiesMenuItems />
    </NavDropdown>
  );
};
