import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './meeting-room.reducer';

export const MeetingRoomDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const meetingRoomEntity = useAppSelector(state => state.meetingRoom.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="meetingRoomDetailsHeading">Meeting Room</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{meetingRoomEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{meetingRoomEntity.name}</dd>
          <dt>
            <span id="capacity">Capacity</span>
          </dt>
          <dd>{meetingRoomEntity.capacity}</dd>
          <dt>
            <span id="requiresApproval">Requires Approval</span>
          </dt>
          <dd>{meetingRoomEntity.requiresApproval ? 'true' : 'false'}</dd>
          <dt>Equipment</dt>
          <dd>
            {meetingRoomEntity.equipment
              ? meetingRoomEntity.equipment.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.name}</a>
                    {meetingRoomEntity.equipment && i === meetingRoomEntity.equipment.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/meeting-room" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/meeting-room/${meetingRoomEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default MeetingRoomDetail;
