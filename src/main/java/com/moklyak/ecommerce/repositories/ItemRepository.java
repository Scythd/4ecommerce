package com.moklyak.ecommerce.repositories;

import com.moklyak.ecommerce.entities.Item;
import com.moklyak.ecommerce.entities.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findDistinctByTags_IdIn(Collection<Long> ids, Pageable pageable);
    List<Item> findByTagsIn(Collection<Tag> tags, Pageable pageable);
    Item findByLibraries_User_EmailAndId(String email, Long id);
    Page<Item> findAllByLibrariesId(Long id, Pageable pageable);

    Page<Item> findDistinctByTagsIn(List<Tag> filter, Pageable withPage);
}
