package fr.vter.xdcc.model;

import org.bson.types.ObjectId;

public interface Repository<TEntity extends EntityWithObjectId> {

  TEntity get(ObjectId id);
}
