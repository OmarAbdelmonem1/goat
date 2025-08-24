import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './booking-request.reducer';

export const BookingRequestDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const bookingRequestEntity = useAppSelector(state => state.bookingRequest.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="bookingRequestDetailsHeading">Booking Request</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{bookingRequestEntity.id}</dd>
          <dt>
            <span id="startTime">Start Time</span>
          </dt>
          <dd>
            {bookingRequestEntity.startTime ? (
              <TextFormat value={bookingRequestEntity.startTime} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="endTime">End Time</span>
          </dt>
          <dd>
            {bookingRequestEntity.endTime ? <TextFormat value={bookingRequestEntity.endTime} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="status">Status</span>
          </dt>
          <dd>{bookingRequestEntity.status}</dd>
          <dt>
            <span id="createdAt">Created At</span>
          </dt>
          <dd>
            {bookingRequestEntity.createdAt ? (
              <TextFormat value={bookingRequestEntity.createdAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="updatedAt">Updated At</span>
          </dt>
          <dd>
            {bookingRequestEntity.updatedAt ? (
              <TextFormat value={bookingRequestEntity.updatedAt} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="purpose">Purpose</span>
          </dt>
          <dd>{bookingRequestEntity.purpose}</dd>
          <dt>Invited Users</dt>
          <dd>
            {bookingRequestEntity.invitedUsers
              ? bookingRequestEntity.invitedUsers.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {i === bookingRequestEntity.invitedUsers.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>

          <dt>Employee</dt>
          <dd>
            {bookingRequestEntity.employee ? (
              <span>
                <a>{bookingRequestEntity.employee.name}</a>
              </span>
            ) : null}
          </dd>

          <dt>Meeting Room</dt>
          <dd>
            {bookingRequestEntity.meetingRoom ? (
              <span>
                <a>{bookingRequestEntity.meetingRoom.name}</a>
              </span>
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/booking-request" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/booking-request/${bookingRequestEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default BookingRequestDetail;
