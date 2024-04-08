import { CurrentUserButton } from "../components/current-user-button";
import { useCurrentUser } from "../hooks/use-current-user";
import { useLogoutMutation } from "../hooks/use-logout-mutation";

const CurrentUserButtonContainer = () => {
  const { data: user, isLoading, isError } = useCurrentUser();

  const logoutMutation = useLogoutMutation();

  if (isLoading) {
    return <p>Загрузка...</p>;
  }

  if (isError) {
    return null;
  }

  return (
    <CurrentUserButton user={user!}>
      <CurrentUserButton.Action
        disabled={logoutMutation.isPending}
        onClick={() => void logoutMutation.mutate()}
      >
        Выйти
      </CurrentUserButton.Action>
    </CurrentUserButton>
  );
};

export { CurrentUserButtonContainer };
