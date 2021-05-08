ALTER TABLE "todo" ADD "is_done" boolean NOT NULL DEFAULT false;
ALTER TABLE "todo" ADD "done_at" bigint;
ALTER TABLE "todo" ADD "created_at" bigint NOT NULL DEFAULT 0;