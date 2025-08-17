import React, { useEffect, useState } from 'react';
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

  const [attachments, setAttachments] = useState([]);

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
    if (!isNew && vacationRequestEntity?.attachments) {
      setAttachments(vacationRequestEntity.attachments);
    }
  }, [vacationRequestEntity]);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const addAttachment = () => {
    setAttachments([...attachments, { name: '', url: '', fileSize: 0, contentType: '', uploadedAt: displayDefaultDateTime() }]);
  };

  const removeAttachment = index => {
    setAttachments(attachments.filter((_, i) => i !== index));
  };

  const handleAttachmentChange = (index, field, value) => {
    const updated = [...attachments];
    updated[index][field] = value;
    setAttachments(updated);
  };

  const saveEntity = values => {
    const entity = {
      ...vacationRequestEntity,
      ...values,
      status: 'PENDING',
      employee: employees.find(it => it.login === account.login),
      attachments: attachments.map(att => ({
        ...att,
        uploadedAt: att.uploadedAt ? convertDateTimeToServer(att.uploadedAt) : displayDefaultDateTime(),
      })),
      createdAt: convertDateTimeToServer(values.createdAt),
      updatedAt: convertDateTimeToServer(values.updatedAt),
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
          status: 'PENDING',
          employee: account?.login,
          attachments: [],
        }
      : {
          type: vacationRequestEntity.type ?? 'ANNUAL',
          status: vacationRequestEntity.status ?? 'PENDING',
          ...vacationRequestEntity,
          createdAt: convertDateTimeFromServer(vacationRequestEntity.createdAt),
          updatedAt: convertDateTimeFromServer(vacationRequestEntity.updatedAt),
          employee: vacationRequestEntity?.employee?.login,
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

              <h5 className="mt-4">Attachments</h5>
              {attachments.map((att, index) => (
                <div key={index} className="attachment-row mb-2 p-2 border rounded">
                  <ValidatedField
                    label="Attachment Name"
                    name={`attachmentName-${index}`}
                    type="text"
                    value={att.name}
                    onChange={e => handleAttachmentChange(index, 'name', e.target.value)}
                    validate={{ required: true }}
                  />
                  <ValidatedField
                    label="Attachment URL"
                    name={`attachmentUrl-${index}`}
                    type="text"
                    value={att.url}
                    onChange={e => handleAttachmentChange(index, 'url', e.target.value)}
                    validate={{ required: true }}
                  />
                  <ValidatedField
                    label="File Size"
                    name={`attachmentFileSize-${index}`}
                    type="number"
                    min="0"
                    value={att.fileSize}
                    onChange={e => handleAttachmentChange(index, 'fileSize', e.target.value)}
                  />
                  <ValidatedField
                    label="Content Type"
                    name={`attachmentContentType-${index}`}
                    type="text"
                    value={att.contentType}
                    onChange={e => handleAttachmentChange(index, 'contentType', e.target.value)}
                  />
                  <ValidatedField
                    label="Uploaded At"
                    name={`attachmentUploadedAt-${index}`}
                    type="datetime-local"
                    value={att.uploadedAt}
                    onChange={e => handleAttachmentChange(index, 'uploadedAt', e.target.value)}
                  />
                  <Button color="danger" type="button" onClick={() => removeAttachment(index)}>
                    Remove
                  </Button>
                </div>
              ))}
              <Button color="secondary" type="button" onClick={addAttachment}>
                Add Attachment
              </Button>

              <div className="mt-4">
                <Button tag={Link} id="cancel-save" to="/vacation-request" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" /> Back
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" /> Save
                </Button>
              </div>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default VacationRequestUpdate;
