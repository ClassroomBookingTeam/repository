import NiceModal, { useModal } from "@ebay/nice-modal-react";
import { DefaultValues } from "react-hook-form";

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";

import { CreateEventFormContainer } from "../containers/create-event-form-container";
import { EventFormData } from "../validation/event-form";

interface Props {
  roomId: string;
  defaultValues?: DefaultValues<EventFormData>;
}

const CreateEventModal = NiceModal.create<Props>(
  ({ roomId, defaultValues }) => {
    const modal = useModal();

    return (
      <Dialog
        open={modal.visible}
        onOpenChange={(open) => (open ? modal.show() : modal.hide())}
      >
        <DialogContent
          // Чтобы юзер случайно не сбросил заполненную форму
          onInteractOutside={(e) => e.preventDefault()}
          className="overflow-y-scroll max-h-screen"
        >
          <DialogHeader>
            <DialogTitle>Новое событие</DialogTitle>
          </DialogHeader>

          <CreateEventFormContainer
            roomId={roomId}
            onSuccess={modal.hide}
            defaultValues={defaultValues}
          />
        </DialogContent>
      </Dialog>
    );
  },
);

export { CreateEventModal };
