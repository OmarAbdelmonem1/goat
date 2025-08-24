import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { getEntities as getMeetingRooms } from 'app/entities/meeting-room/meeting-room.reducer';
import { Status } from 'app/shared/model/enumerations/status.model';
import { createEntity, getEntity, reset, updateEntity } from './booking-request.reducer';

export const BookingRequestUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employees = useAppSelector(state => state.employee.entities);
  const meetingRooms = useAppSelector(state => state.meetingRoom.entities);
  const bookingRequestEntity = useAppSelector(state => state.bookingRequest.entity);
  const loading = useAppSelector(state => state.bookingRequest.loading);
  const updating = useAppSelector(state => state.bookingRequest.updating);
  const updateSuccess = useAppSelector(state => state.bookingRequest.updateSuccess);
  // Get current user from authentication state
  const currentUser = useAppSelector(state => state.authentication.account);

  const handleClose = () => {
    navigate(`/booking-request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getEmployees({}));
    dispatch(getMeetingRooms({}));
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
    values.startTime = convertDateTimeToServer(values.startTime);
    values.endTime = convertDateTimeToServer(values.endTime);

    // Find the current user's employee record
    const currentEmployee = employees.find(emp => emp.user?.login === currentUser?.login);

    const entity = {
      ...bookingRequestEntity,
      ...values,
      status: 'PENDING', // Always set status to PENDING
      invitedUsers: mapIdList(values.invitedUsers),
      employee: currentEmployee, // Set employee from current logged-in user
      meetingRoom: meetingRooms.find(it => it.id.toString() === values.meetingRoom?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  // Get invitable employees (exclude current user)
  const getInvitableEmployees = () => {
    if (!employees || !currentUser?.login) {
      return [];
    }

    // Debug logging
    console.warn('Current user:', currentUser);
    console.warn('All employees:', employees);

    return employees.filter(emp => {
      // Multiple ways to check and exclude current user
      const empLogin = emp.user?.login;
      const empName = emp.name;
      const currentLogin = currentUser.login;
      const currentName = currentUser.firstName || currentUser.lastName;

      // Debug each employee
      console.warn(`Checking employee: ${empName} (login: ${empLogin}) against current user: ${currentLogin}`);

      // Exclude if login matches
      if (empLogin && empLogin === currentLogin) {
        console.warn(`Excluding ${empName} - login match`);
        return false;
      }

      // Exclude if name matches current user
      if (empName && currentName && empName.toLowerCase() === currentName.toLowerCase()) {
        console.warn(`Excluding ${empName} - name match with current user name`);
        return false;
      }

      // Exclude if employee name matches current user login
      if (empName && empName.toLowerCase() === currentLogin?.toLowerCase()) {
        console.warn(`Excluding ${empName} - name matches current user login`);
        return false;
      }

      console.warn(`Including ${empName} in invite list`);
      return true;
    });
  };

  const defaultValues = () =>
    isNew
      ? {
          startTime: displayDefaultDateTime(),
          endTime: displayDefaultDateTime(),
        }
      : {
          ...bookingRequestEntity,
          startTime: convertDateTimeFromServer(bookingRequestEntity.startTime),
          endTime: convertDateTimeFromServer(bookingRequestEntity.endTime),
          invitedUsers: bookingRequestEntity?.invitedUsers?.map(e => e.id.toString()),
          meetingRoom: bookingRequestEntity?.meetingRoom?.id,
        };

  const invitableEmployees = getInvitableEmployees();

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="goatApp.bookingRequest.home.createOrEditLabel" data-cy="BookingRequestCreateUpdateHeading">
            Create or edit a Booking Request
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField name="id" required readOnly id="booking-request-id" label="ID" validate={{ required: true }} />
              ) : null}
              <ValidatedField
                label="Start Time"
                id="booking-request-startTime"
                name="startTime"
                data-cy="startTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="End Time"
                id="booking-request-endTime"
                name="endTime"
                data-cy="endTime"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                }}
              />
              <ValidatedField
                label="Purpose"
                id="booking-request-purpose"
                name="purpose"
                data-cy="purpose"
                type="text"
                validate={{
                  maxLength: { value: 500, message: 'This field cannot be longer than 500 characters.' },
                }}
              />
              <ValidatedField
                label="Invited Users"
                id="booking-request-invitedUsers"
                data-cy="invitedUsers"
                type="select"
                multiple
                name="invitedUsers"
              >
                <option value="" key="0" />
                {invitableEmployees.length > 0 ? (
                  invitableEmployees.map(otherEntity => (
                    <option value={otherEntity.id} key={otherEntity.id}>
                      {otherEntity.name || `Employee ${otherEntity.id}`}
                      {otherEntity.user?.login && ` (${otherEntity.user.login})`}
                    </option>
                  ))
                ) : (
                  <option disabled>No other employees available to invite</option>
                )}
              </ValidatedField>
              <FormText color="muted">Select employees to invite to this meeting. You cannot invite yourself.</FormText>
              <ValidatedField
                id="booking-request-meetingRoom"
                name="meetingRoom"
                data-cy="meetingRoom"
                label="Meeting Room"
                type="select"
                required
                validate={{ required: { value: true, message: 'This field is required.' } }}
              >
                <option value="" key="0" />
                {meetingRooms
                  ? meetingRooms.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.name || otherEntity.id}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/booking-request" replace color="info">
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

export default BookingRequestUpdate;
