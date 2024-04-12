import NiceModal, { useModal } from "@ebay/nice-modal-react";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { EditEventFormContainer } from "../containers/edit-event-form-container";
import { EventInfoContainer } from "../containers/event-info-container";

interface Props {
  eventId: string;
}

const EditEventModal = NiceModal.create<Props>(({ eventId }) => {
  const modal = useModal();

  return (
    <Dialog
      open={modal.visible}
      onOpenChange={(open) => (open ? modal.show() : modal.hide())}
    >
      <DialogContent className="overflow-y-scroll max-h-screen">
        <DialogHeader>
          <DialogTitle>Редактирование события</DialogTitle>
        </DialogHeader>

        <EventInfoContainer eventId={eventId} />

        <EditEventFormContainer
          eventId={eventId}
          onUpdateSuccess={modal.hide}
          onDeleteSuccess={modal.hide}
        />
      </DialogContent>
    </Dialog>
  );
});

export { EditEventModal };
