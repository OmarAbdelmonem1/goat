import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './vacation-request.reducer';

export const VacationRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const vacationRequestEntity = useAppSelector(state => state.vacationRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="vacationRequestDetailsHeading">Vacation Request</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{vacationRequestEntity.id}</dd>
          <dt>
            <span id="startDate">Start Date</span>
          </dt>
          <dd>
            {vacationRequestEntity.startDate ? (
              <TextFormat value={vacationRequestEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="endDate">End Date</span>
          </dt>
          <dd>
            {vacationRequestEntity.endDate ? (
              <TextFormat value={vacationRequestEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="type">Type</span>
          </dt>
          <dd>{vacationRequestEntity.type}</dd>
          <dt>
            <span id="reason">Reason</span>
          </dt>
          <dd>{vacationRequestEntity.reason}</dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{vacationRequestEntity.status}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>
            {vacationRequestEntity.createdAt ? (
              <TextFormat value={vacationRequestEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>
            {vacationRequestEntity.updatedAt ? (
              <TextFormat value={vacationRequestEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>Employee</dt>
          <dd>{vacationRequestEntity.employee.name ? vacationRequestEntity.employee.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/vacation-request" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/vacation-request/${vacationRequestEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default VacationRequestDetail;
