export type FormFieldErrors<T> = {
  [key in keyof T]?: string[]
}
