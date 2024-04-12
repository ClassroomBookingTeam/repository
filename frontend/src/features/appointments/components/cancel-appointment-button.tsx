import { FC } from "react";

import { Button, ButtonProps } from "@/components/ui/button";

const CancelAppointmentButton: FC<ButtonProps> = (props) => {
  return (
    <Button variant="destructive" {...props}>
      Отменить запись
    </Button>
  );
};

export { CancelAppointmentButton };
