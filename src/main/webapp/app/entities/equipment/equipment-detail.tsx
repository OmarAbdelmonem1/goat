import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import {} from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './equipment.reducer';

export const EquipmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const equipmentEntity = useAppSelector(state => state.equipment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="equipmentDetailsHeading">Equipment</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{equipmentEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{equipmentEntity.name}</dd>
          <dt>
            <span id="description">Description</span>
          </dt>
          <dd>{equipmentEntity.description}</dd>
          <dt>
            <span id="isAvailable">Is Available</span>
          </dt>
          <dd>{equipmentEntity.isAvailable ? 'true' : 'false'}</dd>
          <dt>Meeting Rooms</dt>
          <dd>
            {equipmentEntity.meetingRooms
              ? equipmentEntity.meetingRooms.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.id}</a>
                    {equipmentEntity.meetingRooms && i === equipmentEntity.meetingRooms.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/equipment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/equipment/${equipmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default EquipmentDetail;
