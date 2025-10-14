import { describe, expect, it } from 'vitest'
import { usePageRequest } from '@/composables'

describe('usePageRequest tests', () => {
  it('without options should return a page request with default values', () => {
    const { pageRequest } = usePageRequest()
    expect(pageRequest.value.size).toBe(25)
    expect(pageRequest.value.sort).toBe(undefined)
    expect(pageRequest.value.page).toBe(0)
  })

  it('should populate page request with provided options', () => {
    const { pageRequest } = usePageRequest({
      initialSize: 123,
      initialSort: ['sort1'],
      initialPage: 2,
    })
    expect(pageRequest.value.size).toBe(123)
    expect(pageRequest.value.sort).toStrictEqual(['sort1'])
    expect(pageRequest.value.page).toBe(2)
  })

  it('setters should return a new page request with updated values', () => {
    const { pageRequest, setPage, setSize, setSort } = usePageRequest({
      initialSize: 123,
      initialSort: ['sort1'],
      initialPage: 2,
    })

    setPage(3)
    setSize(11)
    setSort(['sort2'])

    expect(pageRequest.value.size).toBe(11)
    expect(pageRequest.value.sort).toStrictEqual(['sort2'])
    expect(pageRequest.value.page).toBe(3)
  })

  it('nextPage and previousPage should return a new page request with updated page value', () => {
    const { pageRequest, nextPage, previousPage } = usePageRequest({
      initialPage: 1,
    })

    expect(pageRequest.value.page).toBe(1)

    nextPage()
    expect(pageRequest.value.page).toBe(2)
    previousPage()
    expect(pageRequest.value.page).toBe(1)
  })
})
