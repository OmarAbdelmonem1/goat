import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './attachment.reducer';

export const AttachmentDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const attachmentEntity = useAppSelector(state => state.attachment.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="attachmentDetailsHeading">Attachment</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{attachmentEntity.id}</dd>
          <dt>
            <span id="name">Name</span>
          </dt>
          <dd>{attachmentEntity.name}</dd>
          <dt>
            <span id="url">Url</span>
          </dt>
          <dd>{attachmentEntity.url}</dd>
          <dt>
            <span id="fileSize">File Size</span>
          </dt>
          <dd>{attachmentEntity.fileSize}</dd>
          <dt>
            <span id="contentType">Content Type</span>
          </dt>
          <dd>{attachmentEntity.contentType}</dd>
          <dt>
            <span id="uploadedAt">Uploaded At</span>
          </dt>
          <dd>
            {attachmentEntity.uploadedAt ? <TextFormat value={attachmentEntity.uploadedAt} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>Vacation Request</dt>
          <dd>{attachmentEntity.vacationRequest ? attachmentEntity.vacationRequest.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/attachment" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/attachment/${attachmentEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default AttachmentDetail;
