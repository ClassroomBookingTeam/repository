import { FC } from "react";

import { DeleteEventButton } from "../components/delete-event-button";
import { useDeleteEventMutation } from "../hooks/use-delete-event-mutation";

interface Props {
  eventId: string;
  onSuccess?: (data: void) => unknown;
}

const DeleteEventButtonContainer: FC<Props> = ({ eventId, onSuccess }) => {
  const { mutate, isPending } = useDeleteEventMutation(eventId, { onSuccess });

  return <DeleteEventButton onClick={() => mutate()} disabled={isPending} />;
};

export { DeleteEventButtonContainer };
