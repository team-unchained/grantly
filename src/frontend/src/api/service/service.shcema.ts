import { z } from 'zod';

export const ServiceSchema = z.object({
  id: z.number().int(),
  name: z.string(),
  description: z.string(),
  imageUrl: z.string(),
});

export type ServiceType = z.infer<typeof ServiceSchema>;
