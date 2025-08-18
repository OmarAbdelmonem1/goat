// src/main/webapp/app/modules/booking-request/my-invitations.tsx
import React, { useEffect, useState } from 'react';
import InfiniteScroll from 'react-infinite-scroll-component';
import { Link, useLocation } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { ASC, DESC, ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import axios from 'axios';

const MyInvitations = () => {
  const location = useLocation();

  const [invitations, setInvitations] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [totalItems, setTotalItems] = useState(0);
  const [links, setLinks] = useState({ next: 0 });

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(location, ITEMS_PER_PAGE, 'id'), location.search),
  );
  const [sorting, setSorting] = useState(false);

  const fetchInvitations = () => {
    setLoading(true);
    axios
      .get('http://localhost:8080/api/v1/booking-requests/my-invitations', {
        params: {
          page: paginationState.activePage - 1,
          size: paginationState.itemsPerPage,
          sort: `${paginationState.sort},${paginationState.order}`,
        },
      })
      .then(res => {
        setInvitations(prev => (paginationState.activePage === 1 ? res.data.content : [...prev, ...res.data.content]));
        setTotalItems(res.data.totalElements);
        setLinks({ next: res.data.totalPages });
      })
      .catch(err => console.error('Error fetching invitations', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchInvitations();
  }, [paginationState.activePage, paginationState.sort, paginationState.order]);

  const handleLoadMore = () => {
    if (paginationState.activePage < links.next) {
      setPaginationState(prev => ({ ...prev, activePage: prev.activePage + 1 }));
    }
  };

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      activePage: 1,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
    setSorting(true);
  };

  const getSortIconByFieldName = (fieldName: string) => {
    if (paginationState.sort !== fieldName) return faSort;
    return paginationState.order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2>My Invitations</h2>
      <div className="table-responsive">
        <InfiniteScroll
          dataLength={invitations.length}
          next={handleLoadMore}
          hasMore={paginationState.activePage < links.next}
          loader={<div className="loader">Loading ...</div>}
        >
          {invitations.length > 0 ? (
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
                  <tr key={`invite-${i}`}>
                    <td>{invite.id}</td>
                    <td>{invite.startTime}</td>
                    <td>{invite.endTime}</td>
                    <td>{invite.meetingRoom?.name || ''}</td>
                    <td>{invite.employee?.id || ''}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            !loading && <div className="alert alert-warning">No invitations found.</div>
          )}
        </InfiniteScroll>
      </div>
    </div>
  );
};

export default MyInvitations;
