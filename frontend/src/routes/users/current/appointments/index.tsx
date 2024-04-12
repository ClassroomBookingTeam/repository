import { startOfDay } from "date-fns";

import { CurrentUserAppointmentsList } from "@/features/appointments";
import { Role, withAuth } from "@/features/users";

const CurrentUserAppointmentsPage = () => {
  return (
    <CurrentUserAppointmentsList
      params={{ date_from: startOfDay(new Date()) }}
    />
  );
};

export default withAuth([Role.Student])(CurrentUserAppointmentsPage);
