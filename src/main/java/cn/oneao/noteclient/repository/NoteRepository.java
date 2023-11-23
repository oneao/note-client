package cn.oneao.noteclient.repository;

import cn.oneao.noteclient.pojo.entity.es.ESNote;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface NoteRepository extends ElasticsearchRepository<ESNote,Integer> {

}
