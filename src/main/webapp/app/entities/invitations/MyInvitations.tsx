// src/main/webapp/app/entities/booking-request/MyInvitations.tsx
import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import axios from 'axios';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';

const MyInvitations = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const [invitations, setInvitations] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalItems, setTotalItems] = useState(0);
  const [error, setError] = useState<string | null>(null);
  const [initialized, setInitialized] = useState(false);

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search),
  );

  const fetchInvitations = async () => {
    console.warn('Fetching invitations...');
    setLoading(true);
    setError(null);

    try {
      const authToken = localStorage.getItem('jhi-authenticationToken') || sessionStorage.getItem('jhi-authenticationToken');

      console.warn('Auth token exists:', !!authToken);
      console.warn('Making request to:', `http://localhost:8080/api/v1/booking-requests/my-invitations`);

      const response = await axios.get(`http://localhost:8080/api/v1/booking-requests/my-invitations`, {
        params: {
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        },
        headers: authToken
          ? {
              Authorization: `Bearer ${authToken}`,
            }
          : {},
      });

      console.warn('API Response:', response.data);
      console.warn('Response status:', response.status);

      // Handle different response structures
      let content = [];
      let total = 0;

      if (response.data) {
        if (Array.isArray(response.data)) {
          // Direct array response
          content = response.data;
          total = response.data.length;
        } else if (response.data.content && Array.isArray(response.data.content)) {
          // Paginated response
          content = response.data.content;
          total = response.data.totalElements || response.data.totalItems || response.data.content.length;
        } else if (response.data.data && Array.isArray(response.data.data)) {
          // Nested data response
          content = response.data.data;
          total = response.data.total || response.data.data.length;
        }
      }

      console.warn('Processed content:', content);
      console.warn('Total items:', total);

      setInvitations(content);
      setTotalItems(total);
      setInitialized(true);
    } catch (err) {
      console.error('Error fetching invitations:', err);
      console.error('Error response:', err.response?.data);
      console.error('Error status:', err.response?.status);

      setError(err.response?.data?.message || err.message || 'Failed to fetch invitations');
      setInvitations([]);
      setTotalItems(0);
      setInitialized(true);
    } finally {
      setLoading(false);
    }
  };

  const sortEntities = () => {
    fetchInvitations();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    console.warn('useEffect triggered, pagination state:', paginationState);
    sortEntities();
  }, [paginationState.activePage, paginationState.sort, paginationState.order]);

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

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) return faSort;
    return order === ASC ? faSortUp : faSortDown;
  };

  console.warn('Rendering MyInvitations:', {
    loading,
    error,
    initialized,
    invitationsLength: invitations.length,
    totalItems,
  });

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-12">
          <h2 id="my-invitations-heading" data-cy="MyInvitationsHeading">
            My Invitations
          </h2>

          {/* Debug info - remove in production */}
          <div className="mb-3 p-2 bg-light rounded">
            <small>
              Debug: Loading: {loading.toString()}, Error: {error || 'none'}, Initialized: {initialized.toString()}, Items:{' '}
              {invitations.length}
            </small>
          </div>

          {/* Always show loading state when loading */}
          {loading && (
            <div className="d-flex justify-content-center p-4">
              <div className="spinner-border" role="status">
                <span className="visually-hidden">Loading...</span>
              </div>
              <div className="ms-2">Loading invitations...</div>
            </div>
          )}

          {/* Show error state */}
          {error && !loading && (
            <div className="alert alert-danger" role="alert">
              <strong>Error:</strong> {error}
              <button
                className="btn btn-sm btn-outline-danger ms-2"
                onClick={() => {
                  setError(null);
                  fetchInvitations();
                }}
              >
                Retry
              </button>
            </div>
          )}

          {/* Show content when not loading and no error */}
          {!loading && !error && initialized && (
            <>
              {invitations.length > 0 ? (
                <div className="table-responsive">
                  <Table responsive striped>
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
                        <th>Meeting Room</th>
                        <th>Organizer</th>
                        <th>Actions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {invitations.map((invite, i) => (
                        <tr key={`invite-${invite.id || i}`} data-cy="entityTable">
                          <td>
                            <Button tag={Link} to={`/booking-request/${invite.id}`} color="link" size="sm">
                              {invite.id}
                            </Button>
                          </td>
                          <td>{invite.startTime ? <TextFormat type="date" value={invite.startTime} format={APP_DATE_FORMAT} /> : 'N/A'}</td>
                          <td>{invite.endTime ? <TextFormat type="date" value={invite.endTime} format={APP_DATE_FORMAT} /> : 'N/A'}</td>
                          <td>{invite.meetingRoom?.name || 'N/A'}</td>
                          <td>{invite.employee?.name || 'N/A'}</td>

                          <td className="text-end">
                            <div className="btn-group flex-btn-group-container" role="group">
                              <Button tag={Link} to={`/booking-request/${invite.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                                <FontAwesomeIcon icon="eye" /> View
                              </Button>
                            </div>
                          </td>
                        </tr>
                      ))}
                    </tbody>
                  </Table>
                </div>
              ) : (
                <div className="alert alert-warning" role="alert">
                  <FontAwesomeIcon icon="exclamation-triangle" className="me-2" />
                  {"No invitations found. You don't have any meeting invitations at the moment."}
                </div>
              )}
            </>
          )}

          {/* Show pagination only when we have items and not loading */}
          {!loading && !error && totalItems > 0 && (
            <div className="row justify-content-center">
              <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} />
            </div>
          )}

          {!loading && !error && totalItems > paginationState.itemsPerPage && (
            <div className="row justify-content-center">
              <JhiPagination
                activePage={paginationState.activePage}
                onSelect={handlePagination}
                maxButtons={5}
                itemsPerPage={paginationState.itemsPerPage}
                totalItems={totalItems}
              />
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default MyInvitations;
