import { z } from 'zod';

export const AppSchema = z.object({
  id: z.number().int(),
  name: z.string(),
  description: z.string(),
  imageUrl: z.string(),
});

export type AppType = z.infer<typeof AppSchema>;
