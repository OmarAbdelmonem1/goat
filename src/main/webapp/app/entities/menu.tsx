import React from 'react';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/employee">
        Employee
      </MenuItem>
      <MenuItem icon="asterisk" to="/meeting-room">
        Meeting Room
      </MenuItem>
      <MenuItem icon="asterisk" to="/equipment">
        Equipment
      </MenuItem>
      <MenuItem icon="asterisk" to="/booking-request">
        Booking Request
      </MenuItem>
      <MenuItem icon="asterisk" to="/vacation-request">
        Vacation Request
      </MenuItem>
      <MenuItem icon="asterisk" to="/attachment">
        Attachment
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
