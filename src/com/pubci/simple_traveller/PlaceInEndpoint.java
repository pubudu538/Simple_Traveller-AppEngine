package com.pubci.simple_traveller;

import com.pubci.simple_traveller.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "placeinendpoint", namespace = @ApiNamespace(ownerDomain = "pubci.com", ownerName = "pubci.com", packagePath = "simple_traveller"))
public class PlaceInEndpoint {

	/**
	 * This method lists all the entities inserted in datastore. It uses HTTP
	 * GET method and paging support.
	 * 
	 * @return A CollectionResponse class containing the list of all entities
	 *         persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listPlaceIn")
	public CollectionResponse<PlaceIn> listPlaceIn(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<PlaceIn> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from PlaceIn as PlaceIn");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<PlaceIn>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and
			// accomodate
			// for lazy fetch.
			for (PlaceIn obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<PlaceIn> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET
	 * method.
	 * 
	 * @param id
	 *            the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getPlaceIn")
	public PlaceIn getPlaceIn(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		PlaceIn placein = null;
		try {
			placein = mgr.find(PlaceIn.class, id);
		} finally {
			mgr.close();
		}
		return placein;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity
	 * already exists in the datastore, an exception is thrown. It uses HTTP
	 * POST method.
	 * 
	 * @param placein
	 *            the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertPlaceIn")
	public PlaceIn insertPlaceIn(PlaceIn placein) {
		EntityManager mgr = getEntityManager();
		try {
			// if (containsPlaceIn(placein)) {
			// throw new EntityExistsException("Object already exists");
			// }
			mgr.persist(placein);
		} finally {
			mgr.close();
		}
		return placein;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does
	 * not exist in the datastore, an exception is thrown. It uses HTTP PUT
	 * method.
	 * 
	 * @param placein
	 *            the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updatePlaceIn")
	public PlaceIn updatePlaceIn(PlaceIn placein) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsPlaceIn(placein)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(placein);
		} finally {
			mgr.close();
		}
		return placein;
	}

	/**
	 * This method removes the entity with primary key id. It uses HTTP DELETE
	 * method.
	 * 
	 * @param id
	 *            the primary key of the entity to be deleted.
	 */
	@ApiMethod(name = "removePlaceIn")
	public void removePlaceIn(@Named("id") Long id) {
		EntityManager mgr = getEntityManager();
		try {
			PlaceIn placein = mgr.find(PlaceIn.class, id);
			mgr.remove(placein);
		} finally {
			mgr.close();
		}
	}

	private boolean containsPlaceIn(PlaceIn placein) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			PlaceIn item = mgr.find(PlaceIn.class, placein.getKey());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
