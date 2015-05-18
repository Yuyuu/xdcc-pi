package fr.vter.xdcc.infrastructure.persistence.mongo;

import fr.vter.xdcc.model.EntityWithObjectId;
import org.bson.types.ObjectId;

public class FakeEntity implements EntityWithObjectId {
  public FakeEntity() {
  }

  @Override
  public ObjectId getId() {
    return id;
  }

  private ObjectId id;
}
