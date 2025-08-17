// src/main/webapp/app/modules/home/home.tsx
import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { useAppSelector } from 'app/config/store';
import { Row, Col, Card, CardBody, CardTitle, CardText, Button, Alert } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <div className="homepage container mt-4">
      <Row className="mb-4">
        <Col md="12" className="text-center">
          <h1 className="display-4 fw-bold">Welcome to the Employee Management System</h1>
          <p className="lead text-muted">Manage schedules, book meeting rooms, and request vacations — all in one place.</p>
          {account?.login ? (
            <Alert color="success">
              You are logged in as <strong>{account.login}</strong>.
            </Alert>
          ) : (
            <Alert color="warning">
              Please{' '}
              <Link to="/login" className="alert-link">
                sign in
              </Link>{' '}
              to access the system. New here?{' '}
              <Link to="/account/register" className="alert-link">
                Register now
              </Link>
              .
            </Alert>
          )}
        </Col>
      </Row>

      <Row>
        {/* Meeting Rooms */}
        <Col md="4" className="mb-4">
          <Card className="shadow-sm h-100">
            <CardBody>
              <CardTitle tag="h5" className="d-flex align-items-center gap-2">
                <FontAwesomeIcon icon="building" />
                Meeting Rooms
              </CardTitle>
              <CardText className="text-muted">
                Browse available rooms, check capacity & equipment, and create bookings with conflict detection.
              </CardText>
              <Button tag={Link} to="/meeting-room" color="primary">
                Manage Rooms
              </Button>
            </CardBody>
          </Card>
        </Col>

        {/* Vacation Requests */}
        <Col md="4" className="mb-4">
          <Card className="shadow-sm h-100">
            <CardBody>
              <CardTitle tag="h5" className="d-flex align-items-center gap-2">
                <FontAwesomeIcon icon="umbrella-beach" />
                Vacation Requests
              </CardTitle>
              <CardText className="text-muted">
                Submit annual or sick leave requests with supporting documents and track approvals by HR.
              </CardText>
              <Button tag={Link} to="/vacation-request" color="primary">
                Request Vacation
              </Button>
            </CardBody>
          </Card>
        </Col>

        {/* Employee Schedules */}
        <Col md="4" className="mb-4">
          <Card className="shadow-sm h-100">
            <CardBody>
              <CardTitle tag="h5" className="d-flex align-items-center gap-2">
                <FontAwesomeIcon icon="user" />
                Employee Schedules
              </CardTitle>
              <CardText className="text-muted">
                View personal schedules, track bookings, and manage your work availability with HR oversight.
              </CardText>
              <Button tag={Link} to="/employee" color="primary">
                View Schedule
              </Button>
            </CardBody>
          </Card>
        </Col>
      </Row>

      {/* Admin/HR Features */}
      {account?.authorities?.includes('ROLE_ADMIN') || account?.authorities?.includes('ROLE_HR') ? (
        <Row className="mt-4">
          <Col md="12">
            <h3 className="mb-3">HR & Admin Features</h3>
          </Col>
          <Col md="6" className="mb-3">
            <Card className="shadow-sm">
              <CardBody>
                <CardTitle tag="h6">
                  <FontAwesomeIcon icon="check-circle" className="me-2" />
                  Approve/Reject Vacation Requests
                </CardTitle>
                <CardText>Review pending vacation requests from employees and manage approvals with ease.</CardText>
              </CardBody>
            </Card>
          </Col>
          <Col md="6" className="mb-3">
            <Card className="shadow-sm">
              <CardBody>
                <CardTitle tag="h6">
                  <FontAwesomeIcon icon="calendar" className="me-2" />
                  Manage Bookings
                </CardTitle>
                <CardText>Create or update meeting room bookings, resolve conflicts, and notify participants.</CardText>
              </CardBody>
            </Card>
          </Col>
        </Row>
      ) : null}
    </div>
  );
};

export default Home;
