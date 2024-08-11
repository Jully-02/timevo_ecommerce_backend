package com.timevo_ecommerce_backend.services.collection;

import com.timevo_ecommerce_backend.dtos.CollectionDTO;
import com.timevo_ecommerce_backend.entities.Collection;
import com.timevo_ecommerce_backend.exceptions.DataNotFoundException;
import com.timevo_ecommerce_backend.repositories.CollectionRepository;
import com.timevo_ecommerce_backend.responses.CollectionResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectionService implements ICollectionService{

    private final CollectionRepository collectionRepository;
    private final ModelMapper modelMapper;
    @Override
    public Collection insertCollection(CollectionDTO collectionDTO) {
        Collection newCollection = modelMapper.map(collectionDTO, Collection.class);
        return collectionRepository.save(newCollection);
    }

    @Override
    public Collection getCollectionById(Long collectionId) throws DataNotFoundException {
        return collectionRepository.findById(collectionId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Collection with ID = " + collectionId));
    }

    @Override
    public Collection updateCollection(Long collectionId, CollectionDTO collectionDTO) throws DataNotFoundException {
        Collection existingCollection = collectionRepository.findById(collectionId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find Collection with ID = " + collectionId));
        existingCollection.setName(collectionDTO.getName());
        return collectionRepository.save(existingCollection);
    }

    @Override
    public void deleteCollection(Long collectionId) {
        collectionRepository.deleteById(collectionId);
    }

    @Override
    public Page<CollectionResponse> getAllCollections(PageRequest pageRequest) {
        return collectionRepository.getAllCollections(pageRequest);
    }
}
