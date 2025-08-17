import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm, isNumber } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEquipment } from 'app/entities/equipment/equipment.reducer';
import { createEntity, getEntity, updateEntity } from './meeting-room.reducer';

export const MeetingRoomUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const equipment = useAppSelector(state => state.equipment.entities);
  const meetingRoomEntity = useAppSelector(state => state.meetingRoom.entity);
  const loading = useAppSelector(state => state.meetingRoom.loading);
  const updating = useAppSelector(state => state.meetingRoom.updating);
  const updateSuccess = useAppSelector(state => state.meetingRoom.updateSuccess);

  const handleClose = () => {
    navigate('/meeting-room');
  };

  useEffect(() => {
    if (!isNew) {
      dispatch(getEntity(id));
    }

    dispatch(getEquipment({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.capacity !== undefined && typeof values.capacity !== 'number') {
      values.capacity = Number(values.capacity);
    }

    const entity = {
      ...meetingRoomEntity,
      ...values,
      equipment: mapIdList(values.equipment),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...meetingRoomEntity,
          equipment: meetingRoomEntity?.equipment?.map(e => e.id.toString()),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="goatApp.meetingRoom.home.createOrEditLabel" data-cy="MeetingRoomCreateUpdateHeading">
            Create or edit a Meeting Room
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? <ValidatedField name="id" required readOnly id="meeting-room-id" label="ID" validate={{ required: true }} /> : null}
              <ValidatedField
                label="Name"
                id="meeting-room-name"
                name="name"
                data-cy="name"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 100, message: 'This field cannot be longer than 100 characters.' },
                }}
              />
              <ValidatedField
                label="Capacity"
                id="meeting-room-capacity"
                name="capacity"
                data-cy="capacity"
                type="text"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  min: { value: 1, message: 'This field should be at least 1.' },
                  max: { value: 500, message: 'This field cannot be more than 500.' },
                  validate: v => isNumber(v) || 'This field should be a number.',
                }}
              />
              <ValidatedField
                label="Requires Approval"
                id="meeting-room-requiresApproval"
                name="requiresApproval"
                data-cy="requiresApproval"
                check
                type="checkbox"
              />
              <ValidatedField label="Equipment" id="meeting-room-equipment" data-cy="equipment" type="select" multiple name="equipment">
                <option value="" key="0" />
                {equipment
                  ? equipment.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.id}
                        {otherEntity.name ? ` - ${otherEntity.name}` : ''}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/meeting-room" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">Back</span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp; Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default MeetingRoomUpdate;
