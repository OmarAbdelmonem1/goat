// src/main/webapp/app/shared/layout/menus/entities.tsx
import React from 'react';
import { NavItem, NavLink } from 'reactstrap';
import { Link as RouterLink } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

// Instead of a dropdown, return individual nav items
export const EntitiesMenuItems = () => {
  return (
    <>
      <NavItem>
        <NavLink tag={RouterLink} to="/employee" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Employee</span>
        </NavLink>
      </NavItem>

      <NavItem>
        <NavLink tag={RouterLink} to="/meeting-room" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Meeting Room</span>
        </NavLink>
      </NavItem>

      <NavItem>
        <NavLink tag={RouterLink} to="/equipment" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Equipment</span>
        </NavLink>
      </NavItem>

      <NavItem>
        <NavLink tag={RouterLink} to="/booking-request" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Booking Request</span>
        </NavLink>
      </NavItem>

      <NavItem>
        <NavLink tag={RouterLink} to="/vacation-request" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Vacation Request</span>
        </NavLink>
      </NavItem>

      {/* <NavItem>
        <NavLink tag={RouterLink} to="/attachment" className="d-flex align-items-center">
          <FontAwesomeIcon icon="asterisk" />
          <span>Attachment</span>
        </NavLink>
      </NavItem> */}
    </>
  );
};

// Keep the original EntitiesMenu for backward compatibility if needed
export const EntitiesMenu = () => {
  return <EntitiesMenuItems />;
};
