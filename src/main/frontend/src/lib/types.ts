export type PartialString<T> = {
  [key in keyof T]?: string
}
