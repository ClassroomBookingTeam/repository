import NiceModal, { useModal } from "@ebay/nice-modal-react";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { EventInfoContainer } from "../containers/event-info-container";
import { ViewEventFormContainer } from "../containers/view-event-form-container";

interface Props {
  eventId: string;
}

const ViewEventModal = NiceModal.create<Props>(({ eventId }) => {
  const modal = useModal();

  return (
    <Dialog
      open={modal.visible}
      onOpenChange={(open) => (open ? modal.show() : modal.hide())}
    >
      <DialogContent className="overflow-y-scroll max-h-screen">
        <DialogHeader>
          <DialogTitle>Просмотр события</DialogTitle>
        </DialogHeader>

        <EventInfoContainer eventId={eventId} />

        <ViewEventFormContainer eventId={eventId} />
      </DialogContent>
    </Dialog>
  );
});

export { ViewEventModal };
