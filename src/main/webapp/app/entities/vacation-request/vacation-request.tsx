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

import { getEntities, updateEntity } from './vacation-request.reducer';

export const VacationRequest = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const vacationRequestList = useAppSelector(state => state.vacationRequest.entities);
  const loading = useAppSelector(state => state.vacationRequest.loading);
  const totalItems = useAppSelector(state => state.vacationRequest.totalItems);
  const updating = useAppSelector(state => state.vacationRequest.updating);

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

  const handleAccept = vacationRequest => {
    const updatedVacationRequest = {
      ...vacationRequest,
      status: 'APPROVED',
      updatedAt: new Date().toISOString(),
    };
    dispatch(updateEntity(updatedVacationRequest));
  };
  const handleReject = vacationRequest => {
    const updatedVacationRequest = {
      ...vacationRequest,
      status: 'REJECTED',
      updatedAt: new Date().toISOString(),
    };
    dispatch(updateEntity(updatedVacationRequest));
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
  const canModifyStatus = status => ['PENDING', 'APPROVED', 'REJECTED'].includes(status);
  return (
    <div>
      <h2 id="vacation-request-heading" data-cy="VacationRequestHeading">
        Vacation Requests
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} /> Refresh list
          </Button>
          <Link to="/vacation-request/new" className="btn btn-primary jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp; Create a new Vacation Request
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {vacationRequestList && vacationRequestList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  ID <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('startDate')}>
                  Start Date <FontAwesomeIcon icon={getSortIconByFieldName('startDate')} />
                </th>
                <th className="hand" onClick={sort('endDate')}>
                  End Date <FontAwesomeIcon icon={getSortIconByFieldName('endDate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  Status <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('reason')}>
                  Reason <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                </th>
                <th className="hand" onClick={sort('requestedBy')}>
                  Requested By <FontAwesomeIcon icon={getSortIconByFieldName('requestedBy')} />
                </th>
                <th className="hand" onClick={sort('createdAt')}>
                  Created At <FontAwesomeIcon icon={getSortIconByFieldName('createdAt')} />
                </th>
                <th>Approval Actions</th>
                <th>Management</th>
              </tr>
            </thead>
            <tbody>
              {vacationRequestList.map((vacationRequest, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/vacation-request/${vacationRequest.id}`} color="link" size="sm">
                      {vacationRequest.id}
                    </Button>
                  </td>
                  <td>
                    {vacationRequest.startDate ? (
                      <TextFormat type="date" value={vacationRequest.startDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {vacationRequest.endDate ? <TextFormat type="date" value={vacationRequest.endDate} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>{vacationRequest.status}</td>
                  <td>{vacationRequest.reason}</td>
                  <td>
                    {vacationRequest.employee?.id ? (
                      <Link to={`/employee/${vacationRequest.employee.id}`}>{vacationRequest.employee.id}</Link>
                    ) : (
                      ''
                    )}
                  </td>

                  <td>
                    {vacationRequest.createdAt ? (
                      <TextFormat type="date" value={vacationRequest.createdAt} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  {/* Approval Actions */}
                  <td>
                    {canModifyStatus(vacationRequest.status) ? (
                      <div className="btn-group">
                        <Button color="success" size="sm" onClick={() => handleAccept(vacationRequest)} disabled={updating}>
                          <FontAwesomeIcon icon="check" /> Accept
                        </Button>
                        <Button color="danger" size="sm" onClick={() => handleReject(vacationRequest)} disabled={updating}>
                          <FontAwesomeIcon icon="times" /> Reject
                        </Button>
                      </div>
                    ) : (
                      <span className="text-muted">No actions</span>
                    )}
                  </td>
                  {/* Management */}
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`/vacation-request/${vacationRequest.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" /> View
                      </Button>
                      <Button tag={Link} to={`/vacation-request/${vacationRequest.id}/edit`} color="primary" size="sm">
                        <FontAwesomeIcon icon="pencil-alt" /> Edit
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/vacation-request/${vacationRequest.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                      >
                        <FontAwesomeIcon icon="trash" /> Delete
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && <div className="alert alert-warning">No Vacation Requests found</div>
        )}
      </div>
      {totalItems ? (
        <div className={vacationRequestList && vacationRequestList.length > 0 ? '' : 'd-none'}>
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

export default VacationRequest;
