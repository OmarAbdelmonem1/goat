import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities, updateEntity } from './booking-request.reducer';

export const BookingRequest = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const bookingRequestList = useAppSelector(state => state.bookingRequest.entities);
  const loading = useAppSelector(state => state.bookingRequest.loading);
  const totalItems = useAppSelector(state => state.bookingRequest.totalItems);
  const updating = useAppSelector(state => state.bookingRequest.updating);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  const handleAccept = bookingRequest => {
    const updatedBookingRequest = {
      ...bookingRequest,
      status: 'APPROVED',
      updatedAt: new Date().toISOString(),
    };
    dispatch(updateEntity(updatedBookingRequest));
  };

  const handleReject = bookingRequest => {
    const updatedBookingRequest = {
      ...bookingRequest,
      status: 'REJECTED',
      updatedAt: new Date().toISOString(),
    };
    dispatch(updateEntity(updatedBookingRequest));
  };

  const getStatusColor = status => {
    switch (status) {
      case 'APPROVED':
        return 'success';
      case 'REJECTED':
        return 'danger';
      case 'PENDING':
        return 'warning';
      default:
        return 'secondary';
    }
  };

  const canModifyStatus = status => {
    return status === 'PENDING';
  };

  return (
    <div>
      <h2 id="booking-request-heading" data-cy="BookingRequestHeading">
        Booking Requests
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/booking-request/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Booking Request
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {bookingRequestList && bookingRequestList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('startTime')}>
                  Start Time <FontAwesomeIcon icon={getSortIconByFieldName('startTime')} />
                </th>
                <th className="hand" onClick={sort('endTime')}>
                  End Time <FontAwesomeIcon icon={getSortIconByFieldName('endTime')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  Created At <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th className="hand" onClick={sort('purpose')}>
                  Purpose <FontAwesomeIcon icon={getSortIconByFieldName('purpose')} />
                </th>
                <th>
                  Employee <FontAwesomeIcon icon="sort" />
                </th>
                <th>
                  Meeting Room <FontAwesomeIcon icon="sort" />
                </th>
                <th>Actions</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {bookingRequestList.map((bookingRequest, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/booking-request/${bookingRequest.id}`} color="link" size="sm">
                      {bookingRequest.id}
                    </Button>
                  </td>
                  <td>
                    {bookingRequest.startTime ? <TextFormat type="date" value={bookingRequest.startTime} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {bookingRequest.endTime ? <TextFormat type="date" value={bookingRequest.endTime} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    <span className={`badge bg-${getStatusColor(bookingRequest.status)}`}>{bookingRequest.status}</span>
                  </td>
                  <td>
                    {bookingRequest.createdAt ? <TextFormat type="date" value={bookingRequest.createdAt} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{bookingRequest.purpose}</td>
                  <td>
                    {bookingRequest.employee ? (
                      <Link to={`/employee/${bookingRequest.employee.id}`}>{bookingRequest.employee.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {bookingRequest.meetingRoom ? (
                      <Link to={`/meeting-room/${bookingRequest.meetingRoom.id}`}>{bookingRequest.meetingRoom.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>
                  <td>
                    {canModifyStatus(bookingRequest.status) ? (
                      <div className="btn-group">
                        <Button
                          color="success"
                          size="sm"
                          onClick={() => handleAccept(bookingRequest)}
                          disabled={updating}
                          data-cy="acceptButton"
                        >
                          <FontAwesomeIcon icon="check" /> Accept
                        </Button>
                        <Button
                          color="danger"
                          size="sm"
                          onClick={() => handleReject(bookingRequest)}
                          disabled={updating}
                          data-cy="rejectButton"
                        >
                          <FontAwesomeIcon icon="times" /> Reject
                        </Button>
                      </div>
                    ) : (
                      <span className="text-muted">No actions available</span>
                    )}
                  </td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/booking-request/${bookingRequest.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" /> <span className="d-none d-md-inline">View</span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/booking-request/${bookingRequest.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/booking-request/${bookingRequest.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" /> <span className="d-none d-md-inline">Delete</span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Booking Requests found</div>
        )}
      </div>
      {totalItems ? (
        <div className={bookingRequestList && bookingRequestList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};
export default BookingRequest;
