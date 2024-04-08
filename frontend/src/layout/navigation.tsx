import { NavLink } from "react-router-dom";

import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from "@/components/ui/navigation-menu";
import { Restricted } from "@/features/users";

const LINKS = [
  {
    to: "/",
    children: "Главная",
    roles: ["teacher", "student"],
  },
  {
    to: "/users/current/events/",
    children: "Мои события",
    roles: ["teacher"],
  },
  {
    to: "/users/current/appointments/",
    children: "Мои записи",
    roles: ["student"],
  },
];

const Navigation = () => {
  return (
    <NavigationMenu>
      <NavigationMenuList>
        {LINKS.map((link) => (
          <Restricted roles={link.roles} key={link.to}>
            <NavigationMenuItem>
              <NavLink to={link.to}>
                {({ isActive }) => (
                  <NavigationMenuLink
                    className={navigationMenuTriggerStyle()}
                    active={isActive}
                  >
                    {link.children}
                  </NavigationMenuLink>
                )}
              </NavLink>
            </NavigationMenuItem>
          </Restricted>
        ))}
      </NavigationMenuList>
    </NavigationMenu>
  );
};

export { Navigation };
