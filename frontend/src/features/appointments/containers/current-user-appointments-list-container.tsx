import { FC } from "react";
import InfiniteScroll from "react-infinite-scroller";

import { EventsList } from "@/features/events";

import { useCurrentUserAppointmentsPaginated } from "../hooks/use-current-user-appointments-paginated";
import { GetCurrentUserAppointmentsParams } from "../types/api";

interface Props {
  params: GetCurrentUserAppointmentsParams;
}

const CurrentUserAppointmentsListContainer: FC<Props> = ({ params }) => {
  const {
    fetchNextPage,
    isFetchingNextPage,
    data,
    hasNextPage,
    isLoading,
    isError,
  } = useCurrentUserAppointmentsPaginated(params);

  if (isLoading) {
    return <p>Загрузка событий...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить события</p>;
  }

  const events =
    data?.pages.flatMap((page) =>
      page.results.flatMap((appointment) => appointment.event),
    ) ?? [];

  return (
    <InfiniteScroll
      loadMore={() => !isFetchingNextPage && fetchNextPage()}
      hasMore={hasNextPage}
      loader={<p key={0}>Загрузка...</p>}
    >
      <EventsList events={events} />
    </InfiniteScroll>
  );
};

export { CurrentUserAppointmentsListContainer };
