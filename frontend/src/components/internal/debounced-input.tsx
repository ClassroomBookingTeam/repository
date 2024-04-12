import { ChangeEventHandler, forwardRef, useEffect, useState } from "react";
import { useDebouncedCallback } from "use-debounce";

import { Input, InputProps } from "../ui/input";

const DebouncedInput = forwardRef<HTMLInputElement, InputProps>(
  ({ value, onChange, ...props }, ref) => {
    const [internalValue, setInternalValue] = useState(value);

    const debouncedOnChange = useDebouncedCallback(
      onChange ?? setInternalValue,
      500,
    );

    const onBeforeChange: ChangeEventHandler<HTMLInputElement> = (event) => {
      setInternalValue(event.target.value);
      debouncedOnChange(event);
    };

    useEffect(() => {
      if (value) {
        setInternalValue(value);
      }
    }, [value]);

    return (
      <Input
        value={internalValue}
        onChange={onBeforeChange}
        {...props}
        ref={ref}
      />
    );
  },
);

DebouncedInput.displayName = "DebouncedInput";

export { DebouncedInput };
