export type LoginCredential = {
  username: string;
  password: string;
}

export type RegistrationData = {
  username: string,
  email: string,
  password: string
}

export type ResetPasswordData = {
  token: string;
  password: string
}