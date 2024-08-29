package com.timevo_ecommerce_backend.services.collection;

import com.timevo_ecommerce_backend.dtos.CollectionDTO;
import com.timevo_ecommerce_backend.entities.Collection;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.responses.collection.CollectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ICollectionService {
    Collection insertCollection (CollectionDTO collectionDTO);

    Collection getCollectionById (Long collectionId) throws DataNotFoundException;

    Collection updateCollection (Long collectionId, CollectionDTO collectionDTO) throws DataNotFoundException;

    void deleteCollection (Long collectionId);

    Page<CollectionResponse> getAllCollections (PageRequest pageRequest);
}
