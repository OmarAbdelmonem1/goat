import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { ValidatedField, ValidatedForm } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { VacationType } from 'app/shared/model/enumerations/vacation-type.model';
import { createEntity, getEntity, reset, updateEntity } from './vacation-request.reducer';

export const VacationRequestUpdate = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const employees = useAppSelector(state => state.employee.entities);
  const account = useAppSelector(state => state.authentication.account);
  const vacationRequestEntity = useAppSelector(state => state.vacationRequest.entity);
  const loading = useAppSelector(state => state.vacationRequest.loading);
  const updating = useAppSelector(state => state.vacationRequest.updating);
  const updateSuccess = useAppSelector(state => state.vacationRequest.updateSuccess);
  const vacationTypeValues = Object.keys(VacationType);

  const handleClose = () => {
    navigate(`/vacation-request${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }
    dispatch(getEmployees({}));
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
    values.createdAt = convertDateTimeToServer(values.createdAt);
    values.updatedAt = convertDateTimeToServer(values.updatedAt);

    if (values.attachmentUploadedAt) {
      values.attachmentUploadedAt = convertDateTimeToServer(values.attachmentUploadedAt);
    }

    const entity = {
      ...vacationRequestEntity,
      ...values,
      status: 'PENDING', // Always set default
      employee: employees.find(it => it.login === account.login), // Match logged-in user
      attachments: [
        {
          id: values.attachmentId,
          name: values.attachmentName,
          url: values.attachmentUrl,
          fileSize: values.attachmentFileSize,
          contentType: values.attachmentContentType,
          uploadedAt: values.attachmentUploadedAt,
        },
      ],
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          createdAt: displayDefaultDateTime(),
          updatedAt: displayDefaultDateTime(),
          attachmentUploadedAt: displayDefaultDateTime(),
          status: 'PENDING',
          employee: account?.login,
        }
      : {
          type: vacationRequestEntity.type ?? 'ANNUAL',
          status: vacationRequestEntity.status ?? 'PENDING',
          ...vacationRequestEntity,
          createdAt: convertDateTimeFromServer(vacationRequestEntity.createdAt),
          updatedAt: convertDateTimeFromServer(vacationRequestEntity.updatedAt),
          employee: vacationRequestEntity?.employee?.login,
          attachmentId: vacationRequestEntity?.attachments?.[0]?.id,
          attachmentName: vacationRequestEntity?.attachments?.[0]?.name,
          attachmentUrl: vacationRequestEntity?.attachments?.[0]?.url,
          attachmentFileSize: vacationRequestEntity?.attachments?.[0]?.fileSize,
          attachmentContentType: vacationRequestEntity?.attachments?.[0]?.contentType,
          attachmentUploadedAt: convertDateTimeFromServer(vacationRequestEntity?.attachments?.[0]?.uploadedAt),
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="goatApp.vacationRequest.home.createOrEditLabel" data-cy="VacationRequestCreateUpdateHeading">
            Create or edit a Vacation Request
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew && <ValidatedField name="id" readOnly id="vacation-request-id" label="ID" validate={{ required: true }} />}
              <ValidatedField
                label="Start Date"
                id="vacation-request-startDate"
                name="startDate"
                type="date"
                validate={{ required: { value: true, message: 'This field is required.' } }}
              />
              <ValidatedField
                label="End Date"
                id="vacation-request-endDate"
                name="endDate"
                type="date"
                validate={{ required: { value: true, message: 'This field is required.' } }}
              />
              <ValidatedField label="Type" id="vacation-request-type" name="type" type="select" validate={{ required: true }}>
                {vacationTypeValues.map(vacationType => (
                  <option value={vacationType} key={vacationType}>
                    {vacationType}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label="Reason"
                id="vacation-request-reason"
                name="reason"
                type="textarea"
                validate={{
                  required: { value: true, message: 'This field is required.' },
                  maxLength: { value: 500, message: 'Reason cannot exceed 500 characters.' },
                }}
              />
              {/* Hidden default status field */}
              <input type="hidden" name="status" value="PENDING" />
              {/* Hidden employee field */}
              <input type="hidden" name="employee" value={account?.login || ''} />
              <h5 className="mt-4">Attachment</h5>
              <ValidatedField label="Attachment Name" name="attachmentName" type="text" validate={{ required: true }} />
              <ValidatedField label="Attachment URL" name="attachmentUrl" type="text" validate={{ required: true }} />
              <ValidatedField label="File Size" name="attachmentFileSize" type="number" min="0" />
              <ValidatedField label="Content Type" name="attachmentContentType" type="text" />
              <ValidatedField label="Uploaded At" name="attachmentUploadedAt" type="datetime-local" placeholder="YYYY-MM-DD HH:mm" />
              <Button tag={Link} id="cancel-save" to="/vacation-request" replace color="info">
                <FontAwesomeIcon icon="arrow-left" /> Back
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" /> Save
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default VacationRequestUpdate;
