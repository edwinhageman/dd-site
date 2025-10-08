INSERT INTO public.grape (grape_id, name)
VALUES (1, 'Chardonnay');
INSERT INTO public.grape (grape_id, name)
VALUES (2, 'Merlot');
INSERT INTO public.grape (grape_id, name)
VALUES (3, 'Cabernet Sauvignon');
INSERT INTO public.grape (grape_id, name)
VALUES (4, 'Pinot Noir');
INSERT INTO public.grape (grape_id, name)
VALUES (5, 'Sauvignon Blanc');

SELECT setval('grape_grape_id_seq', (SELECT MAX(wine_id) FROM wine));
