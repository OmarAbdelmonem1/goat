import React, { useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getMyProfile } from './employee.reducer'; // you need a new thunk for my-profile

export const MyProfile = () => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getMyProfile()); // fetch logged-in user's profile
  }, []);

  const employeeEntity = useAppSelector(state => state.employee.entity);

  return (
    <Row>
      <Col md="8">
        <h2 data-cy="employeeDetailsHeading">My Profile</h2>
        <dl className="jh-entity-details">
          <dt>ID</dt>
          <dd>{employeeEntity.id}</dd>
          <dt>Name</dt>
          <dd>{employeeEntity.name}</dd>
          <dt>Email</dt>
          <dd>{employeeEntity.email}</dd>
          <dt>User Role</dt>
          <dd>{employeeEntity.userRole}</dd>
          <dt>Created At</dt>
          <dd>{employeeEntity.createdAt ? <TextFormat value={employeeEntity.createdAt} type="date" format={APP_DATE_FORMAT} /> : null}</dd>
          <dt>Vacation Balance</dt>
          <dd>{employeeEntity.vacationBalance}</dd>
          <dt>User ID</dt>
          <dd>{employeeEntity.user ? employeeEntity.user.id : ''}</dd>
          <dt>Invitations</dt>
          <dd>
            {employeeEntity.invitations
              ? employeeEntity.invitations.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {i === employeeEntity.invitations.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/employee/${employeeEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MyProfile;
