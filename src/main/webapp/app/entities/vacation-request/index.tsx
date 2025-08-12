import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import VacationRequest from './vacation-request';
import VacationRequestDetail from './vacation-request-detail';
import VacationRequestUpdate from './vacation-request-update';
import VacationRequestDeleteDialog from './vacation-request-delete-dialog';

const VacationRequestRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<VacationRequest />} />
    <Route path="new" element={<VacationRequestUpdate />} />
    <Route path=":id">
      <Route index element={<VacationRequestDetail />} />
      <Route path="edit" element={<VacationRequestUpdate />} />
      <Route path="delete" element={<VacationRequestDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default VacationRequestRoutes;
