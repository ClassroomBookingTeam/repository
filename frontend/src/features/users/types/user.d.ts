import { Role } from "./role";

interface User {
  id: string;
  email: string;
  roles: Role[];
  first_name: string;
  last_name: string;
  middle_name: string | null;
  short_name: string;
  full_name: string;
}

export { User };
