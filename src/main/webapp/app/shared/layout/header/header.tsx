// src/main/webapp/app/shared/layout/header/header.tsx
import React, { useState } from 'react';
import { Translate } from 'react-jhipster';
import { Navbar, Nav, NavbarToggler, Collapse, NavItem, NavLink } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link as RouterLink, useLocation } from 'react-router-dom';

import { Brand, Home } from './header-components';
import { AdminMenu, AccountMenu } from '../menus';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isOpenAPIEnabled: boolean;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);
  const location = useLocation();

  const toggleMenu = () => setMenuOpen(!menuOpen);

  // Check if current path matches the nav item
  const isActiveRoute = (path: string) => {
    return location.pathname.startsWith(path);
  };

  const renderAuthenticated = () => {
    const { isAdmin, isOpenAPIEnabled } = props;

    return (
      <Nav id="header-tabs" className="ms-auto align-items-center" navbar>
        {/* Home */}
        <NavItem className="mx-1">
          <NavLink
            tag={RouterLink}
            to="/"
            className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${isActiveRoute('/') && location.pathname === '/' ? 'active-nav' : ''}`}
          >
            <FontAwesomeIcon icon="home" className="me-2" />
            <span className="nav-text">Home</span>
          </NavLink>
        </NavItem>

        {/* Employees - visible only for ADMIN */}
        {props.isAdmin && (
          <NavItem className="mx-1">
            <NavLink
              tag={RouterLink}
              to="/employee"
              className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${
                isActiveRoute('/employee') ? 'active-nav' : ''
              }`}
            >
              <FontAwesomeIcon icon="users" className="me-2 text-primary" />
              <span className="nav-text">Employees</span>
            </NavLink>
          </NavItem>
        )}

        {/* Meeting Rooms - visible only for ADMIN */}
        {props.isAdmin && (
          <NavItem className="mx-1">
            <NavLink
              tag={RouterLink}
              to="/meeting-room"
              className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${
                isActiveRoute('/meeting-room') ? 'active-nav' : ''
              }`}
            >
              <FontAwesomeIcon icon="door-open" className="me-2 text-success" />
              <span className="nav-text">Meeting Rooms</span>
            </NavLink>
          </NavItem>
        )}

        {/* Equipment - visible only for ADMIN */}
        {props.isAdmin && (
          <NavItem className="mx-1">
            <NavLink
              tag={RouterLink}
              to="/equipment"
              className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${
                isActiveRoute('/equipment') ? 'active-nav' : ''
              }`}
            >
              <FontAwesomeIcon icon="laptop" className="me-2 text-info" />
              <span className="nav-text">Equipment</span>
            </NavLink>
          </NavItem>
        )}

        {/* Bookings */}
        <NavItem className="mx-1">
          <NavLink
            tag={RouterLink}
            to="/booking-request"
            className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${isActiveRoute('/booking-request') ? 'active-nav' : ''}`}
          >
            <FontAwesomeIcon icon="calendar-check" className="me-2 text-warning" />
            <span className="nav-text">Bookings</span>
          </NavLink>
        </NavItem>

        {/* Vacations */}
        <NavItem className="mx-1">
          <NavLink
            tag={RouterLink}
            to="/vacation-request"
            className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${isActiveRoute('/vacation-request') ? 'active-nav' : ''}`}
          >
            <FontAwesomeIcon icon="plane-departure" className="me-2 text-danger" />
            <span className="nav-text">Vacations</span>
          </NavLink>
        </NavItem>

        {/* Attachments
        <NavItem className="mx-1">
          <NavLink
            tag={RouterLink}
            to="/attachment"
            className={`nav-link-enhanced d-flex align-items-center px-3 py-2 rounded-pill ${isActiveRoute('/attachment') ? 'active-nav' : ''}`}
          >
            <FontAwesomeIcon icon="paperclip" className="me-2 text-secondary" />
            <span className="nav-text">Attachments</span>
          </NavLink>
        </NavItem> */}

        {/* Divider */}
        <div className="nav-divider mx-2"></div>

        {/* Admin Menu */}
        {isAdmin && (
          <div className="admin-menu-wrapper">
            <AdminMenu showOpenAPI={isOpenAPIEnabled} />
          </div>
        )}

        {/* Account Menu */}
        <div className="account-menu-wrapper">
          <AccountMenu isAuthenticated={props.isAuthenticated} />
        </div>
      </Nav>
    );
  };

  const renderGuest = () => (
    <Nav id="header-tabs" className="ms-auto" navbar>
      <AccountMenu isAuthenticated={props.isAuthenticated} />
    </Nav>
  );

  return (
    <div id="app-header">
      <Navbar data-cy="navbar" light expand="lg" fixed="top" className="enhanced-navbar shadow-sm">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} className="border-0" />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          {props.isAuthenticated ? renderAuthenticated() : renderGuest()}
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
