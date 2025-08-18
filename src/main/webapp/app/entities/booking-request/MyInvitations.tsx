// src/main/webapp/app/modules/booking-request/my-invitations.tsx
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
  const [error, setError] = useState<string | null>(null); // Add error state for debugging

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search),
  );

  const fetchInvitations = () => {
    setLoading(true);
    setError(null); // Reset error state

    const authToken = localStorage.getItem('jhi-authenticationToken');

    axios
      .get(`http://localhost:8080/api/v1/booking-requests/my-invitations`, {
        params: {
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        },
        // Add headers for authentication if needed
        headers: authToken
          ? {
              Authorization: `Bearer ${authToken}`,
            }
          : {},
      })
      .then(res => {
        const content = res.data.content || res.data || [];
        const total = res.data.totalElements || res.data.length || 0;

        setInvitations(content);
        setTotalItems(total);
      })
      .catch(err => {
        console.error('Error fetching invitations:', err);
        console.error('Error response:', err.response?.data);
        console.error('Error status:', err.response?.status);
        setError(err.response?.data?.message || err.message || 'Failed to fetch invitations');
      })
      .finally(() => setLoading(false));
  };

  const sortEntities = () => {
    fetchInvitations();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (location.search !== endURL) {
      navigate(`${location.pathname}${endURL}`);
    }
  };

  useEffect(() => {
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

  return (
    <div>
      <h2>My Invitations</h2>

      {/* Show loading state */}
      {loading && <div className="alert alert-info">Loading invitations...</div>}

      {/* Show error state */}
      {error && <div className="alert alert-danger">Error: {error}</div>}

      <div className="table-responsive">
        {!loading && !error && invitations.length > 0 ? (
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
                <th>Meeting Room</th>
                <th>Organizer</th>
              </tr>
            </thead>
            <tbody>
              {invitations.map((invite, i) => (
                <tr key={`invite-${invite.id || i}`}>
                  <td>{invite.id}</td>
                  <td>{invite.startTime ? <TextFormat type="date" value={invite.startTime} format={APP_DATE_FORMAT} /> : 'N/A'}</td>
                  <td>{invite.endTime ? <TextFormat type="date" value={invite.endTime} format={APP_DATE_FORMAT} /> : 'N/A'}</td>
                  <td>{invite.meetingRoom?.name || 'N/A'}</td>
                  <td>{invite.employee?.id || invite.organizer?.name || 'N/A'}</td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && !error && <div className="alert alert-warning">No invitations found.</div>
        )}
      </div>

      {totalItems > 0 && (
        <div className="d-flex justify-content-center">
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
  );
};

export default MyInvitations;
