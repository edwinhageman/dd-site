INSERT INTO public.wine_style (wine_style_id, name)
VALUES (1, 'Sparkling');
INSERT INTO public.wine_style (wine_style_id, name)
VALUES (2, 'Red');
INSERT INTO public.wine_style (wine_style_id, name)
VALUES (3, 'White');
INSERT INTO public.wine_style (wine_style_id, name)
VALUES (4, 'Orange');
INSERT INTO public.wine_style (wine_style_id, name)
VALUES (5, 'Dessert');

SELECT setval('wine_style_wine_style_id_seq', (SELECT MAX(wine_id) FROM wine));
