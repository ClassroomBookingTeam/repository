import { FC } from "react";
import InfiniteScroll from "react-infinite-scroller";

import { EventsList } from "../components/events-list";
import { useEventsPaginated } from "../hooks/use-events-paginated";
import { GetEventsParams } from "../types/api";

interface Props {
  params?: GetEventsParams;
}

const EventsListContainer: FC<Props> = ({ params }) => {
  const {
    fetchNextPage,
    isFetchingNextPage,
    data,
    hasNextPage,
    isLoading,
    isError,
  } = useEventsPaginated(params);

  if (isLoading) {
    return <p>Загрузка событий...</p>;
  }

  if (isError) {
    return <p>Не удалось загрузить события</p>;
  }

  const events = data?.pages.flatMap((page) => page.results) ?? [];

  return (
    <InfiniteScroll
      loadMore={() => !isFetchingNextPage && fetchNextPage()}
      hasMore={hasNextPage}
      loader={
        <p key={0} className="mt-4 text-center">
          Загрузка...
        </p>
      }
    >
      <EventsList events={events} />
    </InfiniteScroll>
  );
};

export { EventsListContainer };
