import { CurrentUserEventsCalendar } from "@/features/events";
import { Role, withAuth } from "@/features/users";

const CurrentUserEventsPage = () => {
  return <CurrentUserEventsCalendar />;
};

export default withAuth([Role.Teacher])(CurrentUserEventsPage);
